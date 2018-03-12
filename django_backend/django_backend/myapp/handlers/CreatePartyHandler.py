from django.http import HttpResponse
import json
from myapp.models import User
from myapp.models import Party


# TODO: DB look ups are case sensitive, should store all lowercase or whatever
def passOff(json_data):
    email = json_data['host']['email']
    first_name = json_data['host']['first_name']
    last_name = json_data['host']['last_name']
    party_name = json_data['party_name']

    user = None
    try:
        user = User.objects.get(first_name=first_name, last_name=last_name)
    except User.DoesNotExist:
        data = {}

    party = Party(name=party_name, unique_code="123456", host=user)
    party.save()

    data = party.to_dict()
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json')