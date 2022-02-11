import numpy as np
from array import *
import copy
import os
import scipy.spatial.distance as spdist

GLOBAL_PATH = "."  # set the location of the file where everything should be stored

# function to read given file as array
def read(filed):
    dest = GLOBAL_PATH + filed
    with open(dest, 'r') as file:
        Str = file.read()

    lines = []
    lines = Str.split('\n');

    ar = []
    print("loading...%s" % filed)
    for i in range(len(lines)):
        ar.append(lines[i].split(','))

    print("succesful load.")

    print("matrix creation...")
    for i in range(len(ar)):
        for j in range(len(ar[i])):
            ar[i][j] = float(ar[i][j])
    return ar


# Returns a list of indexes sorted by their respective values
def order(q):
    temp = copy.deepcopy(q)
    order = []
    prev = -1
    for i in range(len(temp)):
        now = q.index(max(temp))
        if prev == now:
            q[prev] = q[prev] + 0.000000000001
            now = q.index(max(temp))
        order.append(now)
        temp.remove(max(temp))
        prev = now

    return order


def writer(w, r, j):
    dest = GLOBAL_PATH + "results.txt"
    f = open(dest, "a")
    for i in range(50):
        f.write(str(j))
        f.write(" Q0 ")
        f.write(str(w[i]))
        f.write(" 0 ")
        f.write(str(r[w[i]]))
        f.write(" 1\n")

# read matrices
Uk = read("Uk.text")
Sk = read("Sk.text")
Vk = read("Vk.text")

print("matrixes created.")

print("loading queries...")
with open(GLOBAL_PATH + "txq.text", 'r') as file:
    Str = file.read()

lines = []
lines = Str.split('\n')

txq = []

for i in range(len(lines)):
    txq.append(lines[i].split(','))

for i in range(len(txq)):
    for j in range(len(txq[i])):
        txq[i][j] = float(txq[i][j])

print("queries loaded.")

print("Analysing similarity...")

# 1st Way (Used in actual calculations)

# Calculate Ak
Ak = np.matmul(Uk, Sk)
Ak = np.matmul(Ak, Vk)

# get transposed qurey matrix
numpy_array = np.array(txq)
transpose = numpy_array.T
tt = transpose.tolist()

# delete results file from previous runs
if os.path.isfile(GLOBAL_PATH + "results.txt"):
    os.remove( GLOBAL_PATH + "results.txt")

# Calculate similarity
for i in range(len(tt)):
    qrels = np.matmul(tt[i], Ak) / (np.linalg.norm(tt[i]) * np.linalg.norm(Ak))

    qrels = qrels.tolist()

    writer(order(qrels), qrels, (i + 1))

print("Results saved.")

# 2nd Way: qTk=qT*Uk*Sk

# numpy_array = np.array(Vk)
# transpose = numpy_array.T
# tv = transpose.tolist()

# numpy_array = np.array(txq)
# transpose = numpy_array.T
# tt = transpose.tolist()


# ka8e erwtish
# query=[]

# similarity of each query with documents
# results=[]

# for i in range(len(tt)):
# Q=np.matmul(tt[i],Uk)
# Q=np.matmul(Q,Sk)
# results=[]
# for j in range(len(tv)):
# results.append(np.matmul(Q, tv[j]) / (np.linalg.norm(Q) * np.linalg.norm(tv[j])))map=0.0059 k=300
# results.append(spdist.cosine(Q,tv[j])) map=0.0010 k=300
# query.append(results)