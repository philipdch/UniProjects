import io
import csv 
import os
import re

from minio import Minio
import json


def handle(req):
    client = Minio(
        "172.17.0.1:9000",
        access_key="minioadmin",
        secret_key="minioadmin",
        secure=False,
    )

    #Request is expected to contain a Minio event in json format
    y = json.loads(req)

    key = y["Key"] #Value contains info about the bucket and file which triggered the event in format /bucket_name/file_name

    bucket = key.split("/")[0]
    file = key.split("/")[1]

    print(bucket)
    print(file)

    try:
        response = client.get_object(bucket, file) # query bucket to get uploaded file
        # Read data from response.
        resp_body = response.data.decode("UTF-8") # get response body in UTF-8
        
        intermediate_results = mapper(resp_body) # calculate intermediate results
        intermediate_results = convertToString(intermediate_results) # convert [key, value] list ouput to string in CSV format
        print(intermediate_results)

        part = extractPartNumber(file)
        print(part)
        if part is not None:
            filename = "intermediate_results_" + str(part) + ".csv"
        else:
            filename = "intermediate_results.csv"
        print(filename)
        # write CSV to bucket 
        result = client.put_object(
            "mapper", filename, io.BytesIO(str.encode(intermediate_results)), length=-1, part_size=10 * 1024 * 1024,
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

# produces intermediate results. Given a CSV file maps each starting station and its coordinates
# in a row to a quadrant of a circle with center $center_latitude and longitute $center_longitude
# The result is a list of [key, value] pairs where the key is the quadrant id with value 1 
def mapper(chunk):

    center_latitude = float(40.735923)
    center_longitude = float(-73.990294)

    map_output = []

    data = csv.reader(io.StringIO(chunk), delimiter=",", quotechar='"')
    header = next(data)
    print(header)
    for row in data:
        # print(row)
        lat = float(row[5])
        long = float(row[6])

        if lat < center_latitude and long > center_longitude:
            map_output.append(["q1",1])
        elif lat > center_latitude and long > center_longitude:
            map_output.append(["q2",1])
        elif lat > center_latitude and long < center_longitude:
            map_output.append(["q3",1])
        else:
            map_output.append(["q4",1])
    return map_output

# Converts a list of lists to a string in CSV format in order to be encoded and written to a bucket
def convertToString(list):
    joined = ""
    for inner_list in list:
        joined += ','.join(str(x) for x in inner_list)
        joined += '\n'
    return joined

def extractPartNumber(file):

    pattern = r"_part(\d+)"
    match = re.search(pattern, file)
    print(file)
    print(match)
    if match:
        part_number = int(match.group(1))
        return part_number
    return None

# Debug only
# if __name__ == "__main__":
#                                               "Key":"BUCKET_ID/FILE_ID"
#     j = '{"EventName":"s3:ObjectCreated:Put","Key":"test/test2.txt","Records":[{"eventVersion":"2.0","eventSource":"minio:s3","awsRegion":"","eventTime":"2022-03-15T10:47:54.912Z","eventName":"s3:ObjectCreated:Put","userIdentity":{"principalId":"minioadmin"},"requestParameters":{"principalId":"minioadmin","region":"","sourceIPAddress":"10.0.1.3"},"responseElements":{"content-length":"0","x-amz-request-id":"16DC88295423EAF5","x-minio-deployment-id":"6b68a4ce-cce8-4ddc-bd0b-b18a434c6c66","x-minio-origin-endpoint":"http://10.0.1.3:9000"},"s3":{"s3SchemaVersion":"1.0","configurationId":"Config","bucket":{"name":"test","ownerIdentity":{"principalId":"minioadmin"},"arn":"arn:aws:s3:::test"},"object":{"key":"input.json","size":649,"eTag":"015a2b496dfdd0416a405ea2ef825190","contentType":"application/json","userMetadata":{"content-type":"application/json"},"sequencer":"16DC88295AFFFD7B"}},"source":{"host":"10.0.1.3","port":"","userAgent":"MinIO (linux; amd64) minio-go/v7.0.23"}}]}';
#
#     handle(j)