import io
import csv
from xmlrpc.server import list_public_methods 

from minio import Minio
import json

TOTAL_PARTS = 4

def handle(req):
    client = Minio(
        "172.17.0.1:9000",
        access_key="minioadmin",
        secret_key="minioadmin",
        secure=False,
    )

    y = json.loads(req)

    key = y["Key"]

    bucket = key.split("/")[0]
    file = key.split("/")[1]

    print(bucket)
    print(file)

    try:

        objects = client.list_objects(bucket, prefix="intermediate_results")
        object_list = list(objects)
        num_objects = sum(1 for x in object_list)
        print(num_objects)

        #Reduce function is expected to be executed only when all the intermediate results are present in the specified bucket
        if num_objects == TOTAL_PARTS:
            reduce_output = {"q1":0, "q2":0, "q3":0, "q4":0}
            final_results = reduce_output
            for file in object_list:
                print(file)
                response = client.get_object(bucket, file.object_name)
                # Read data from response.
                resp_body = response.data.decode("UTF-8")
                # print(resp_body)

                final_results = reducer(resp_body, reduce_output)
            final_results = convertToString(final_results)
            print(final_results)

            result = client.put_object(
                "reducer", "final_results.json", io.BytesIO(str.encode(final_results)), length=-1, part_size=10 * 1024 * 1024,
            )
            print(
                "created {0} object; etag: {1}, version-id: {2}".format(
                    result.object_name, result.etag, result.version_id,
                ),
            )

    finally:
        response.close()
        response.release_conn()

    return str("ok")

#aggregates intermediate results, producing 4 unique sums, one for each geographical quarter
def reducer(chunk, output_dict):

    data = csv.reader(io.StringIO(chunk), delimiter=",", quotechar='"')
    for row in data:
        key = row[0]
        value = int(row[1])
        output_dict[key] += value
    return output_dict

def convertToString(input):
    return json.dumps(input)

# Debug only
# if __name__ == "__main__":
#                                               "Key":"BUCKET_ID/FILE_ID"
#     j = '{"EventName":"s3:ObjectCreated:Put","Key":"test/test2.txt","Records":[{"eventVersion":"2.0","eventSource":"minio:s3","awsRegion":"","eventTime":"2022-03-15T10:47:54.912Z","eventName":"s3:ObjectCreated:Put","userIdentity":{"principalId":"minioadmin"},"requestParameters":{"principalId":"minioadmin","region":"","sourceIPAddress":"10.0.1.3"},"responseElements":{"content-length":"0","x-amz-request-id":"16DC88295423EAF5","x-minio-deployment-id":"6b68a4ce-cce8-4ddc-bd0b-b18a434c6c66","x-minio-origin-endpoint":"http://10.0.1.3:9000"},"s3":{"s3SchemaVersion":"1.0","configurationId":"Config","bucket":{"name":"test","ownerIdentity":{"principalId":"minioadmin"},"arn":"arn:aws:s3:::test"},"object":{"key":"input.json","size":649,"eTag":"015a2b496dfdd0416a405ea2ef825190","contentType":"application/json","userMetadata":{"content-type":"application/json"},"sequencer":"16DC88295AFFFD7B"}},"source":{"host":"10.0.1.3","port":"","userAgent":"MinIO (linux; amd64) minio-go/v7.0.23"}}]}';
#
#     handle(j)