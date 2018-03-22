from django.http import HttpResponse
import json
from myapp.models import User
from myapp.models import Song


# TODO: DB look ups are case sensitive, should store all lowercase or whatever
def passOff(json_data):
    email = json_data['user']['email']
    address = json_data['address']
    port = json_data['port']

    try:
        user = User.objects.get(email=email)
        user.address = address
        user.port = port
        user.save()
    except User.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    data = {}
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json',
                        status=200)