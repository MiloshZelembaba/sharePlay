import json
from myapp.models import User


'''
So this is a very temporary method and implementation just to make debugging easier
It basically allows me to not have to specify the client IP address in the client that it sends to the
server. 
'''

def updateNetworkInfo(request):
    received_json_data = json.loads(request.body.decode("utf-8"))
    remote_address = request.META.get('REMOTE_ADDR')  # this shouldn't be relied on, bad practice
    email = received_json_data['user']['email']

    try:
        user = User.objects.get(email=email)
        user.address = remote_address
        user.save()
    except User.DoesNotExist:
        print("couldn't do network update for some reason")
