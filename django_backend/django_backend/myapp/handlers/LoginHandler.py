from django.http import HttpResponse
import json
from myapp.models import User


def passOff(json_data):
    email = json_data['email']

    try:
        user = User.objects.get(email=email)
        data = user.to_dict()
    except User.DoesNotExist:
        data = {}

    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json')



