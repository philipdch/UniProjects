import requests

# JSON payload to be delivered
payload = {"captchaId":2,"captcha":"9","comment":"You will never reach the truth (anonymous)","rating":1}

url = "http://localhost:3000/api/Feedbacks/"
for i in range(20):
    try:
        post_response = requests.post(url, json = payload)
        post_response_json = post_response.json()
        print(post_response_json)
        post_response.raise_for_status()
    except requests.exceptions.HTTPError as error:
        print(error)
