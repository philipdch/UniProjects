from pyspark.sql import SparkSession
from pyspark.sql.types import StructType,StructField, StringType, FloatType
import matplotlib.pyplot as plt
import pandas as pd
import os
import shutil

spark = SparkSession.builder.master("local[*]").appName("Big Data Project").getOrCreate()

#---------- Report 1 ----------
productsDF = spark.read.options(header = True).csv("project2Dataset/products.csv")
deptDF = spark.read.options(header = True).csv("project2Dataset/departments.csv")

productsDF.createOrReplaceTempView("products")
deptDF.createOrReplaceTempView("departments")

result1 = spark.sql("""select department, count(*) as total_products
                        from products join departments using(department_id)
                        group by department
                        order by department""")

result1.show()

result1 = result1.toPandas()

if os.path.exists('results1'):
    shutil.rmtree('results1')
    print('overwritten result 1')
result1.to_csv('results1.csv', index = False)

result1.set_index('department').plot(kind="bar")
plt.title("Products per Department")
plt.xlabel("Department")
plt.ylabel("Total products")
plt.tight_layout()
plt.savefig('chart1.pdf')

#---------- Report 2 ----------
ordersDF = spark.read.options(header = True).csv("project2Dataset/orders.csv")
ordersDF.createOrReplaceTempView("orders")

result2 = spark.sql("""select order_dow, count(*) as total_orders
                     from orders
                     group by order_dow
                     order by order_dow""")

result2.show()
result2 = result2.toPandas()

dow = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

if os.path.exists('results2'):
    shutil.rmtree('results2')
    print('overwritten result 2')
result2.to_csv('results2.csv', index = False)

plt.clf()

explode = [0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05]
plt.pie(result2['total_orders'], labels = dow, explode=explode, autopct='%1.1f%%')
plt.title('Total orders for each day of the week')
plt.savefig("chart2.pdf")

#---------- Report 3 ----------

orderProductsDF = spark.read.options(header = True).csv("project2Dataset/order_products.csv")
orderProductsDF.createOrReplaceTempView("orderProducts")

result3 = spark.sql("""select department, orderProducts.product_id, product_name
                     from orderProducts, departments, products
                     where orderProducts.product_id = products.product_id and departments.department_id = products.department_id
                     group by department, orderProducts.product_id, product_name
                     having sum(reordered) = 0
                     order by department, product_name""")

result3.show()
result3 = result3.toPandas()

if os.path.exists('results3'):
    shutil.rmtree('results3')
    print('overwritten result 3')
result3.to_csv('results3.csv', index = False)

#---------- Report 4 ----------

result4 = spark.sql("""select department, product_name, sum(reordered) as total_reorders
                       from departments, products, orderProducts
                       where products.product_id = orderProducts.product_id and products.department_id = departments.department_id
                       group by department, product_name
                       order by department, sum(reordered) DESC""")

tempRDD = result4.rdd.collect()
tempResults = []

maxKey = tempRDD[0]['department']
currentKey = tempRDD[0]['total_reorders']

for row in tempRDD:
    if(currentKey != row['department']):
        currentKey = row['department']
        maxKey = row['total_reorders']
        tempResults.append(row)
        continue

    if(row['total_reorders'] == maxKey):
        tempResults.append(row)

result4 = spark.createDataFrame(tempResults)
result4.show(200)

result4 = result4.toPandas()

if os.path.exists('results4'):
    shutil.rmtree('results4')
    print('overwritten result 4')
result4.to_csv('results4.csv', index = False)

#---------- Report 5 ----------

aislesDF = spark.read.options(header = True).csv("project2Dataset/aisles.csv")
aislesDF.createOrReplaceTempView("aisles")

denominator = spark.sql("""select aisles.aisle, count(*) as denominator
                        from aisles, products
                        where aisles.aisle_id = products.aisle_id
                        group by aisles.aisle
                        order by aisles.aisle""")

numerator = spark.sql("""select aisles.aisle, count(*) as numerator
                        from aisles, products
                        where aisles.aisle_id = products.aisle_id and products.product_id in (select product_id from orderProducts where add_to_cart_order = 1)
                        group by aisles.aisle
                        order by aisles.aisle""") 

result5 = denominator.join(numerator, 'aisle').rdd.map(lambda x: (x['aisle'], str(round(100*x['numerator']/x['denominator'], 2))))

schema = StructType([ \
    StructField("aisle",StringType(),True), \
    StructField("bought_first_percentage",StringType(),True), 
  ])

result5 = spark.createDataFrame(data=result5, schema=schema).orderBy('aisle').toPandas()
if os.path.exists('results5'):
    shutil.rmtree('results5')
    print('overwritten result 4')
result5.to_csv('results5.csv', index = False)

spark.close()