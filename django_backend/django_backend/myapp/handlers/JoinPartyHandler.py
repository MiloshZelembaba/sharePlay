from django.http import HttpResponse
import json
from myapp.models import User
from myapp.models import Party


def passOff(json_data):
    party_code = json_data['code']
    first_name = json_data['first_name']
    last_name = json_data['last_name']

    party = None
    try:
        party = Party.objects.get(unique_code=party_code)
    except User.DoesNotExist:
        data = {}

    user = User.objects.get(first_name=first_name, last_name=last_name)
    user.current_party = party
    user.save()

    data = party.to_dict()
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json')