from django.http import HttpResponse
import json
from myapp.models import User


def passOff(json_data):
    user_id = json_data['user']['id']

    user = None
    try:
        user = User.objects.get(id=user_id)
        ## TODO: when the user is the host
        user.current_party = None
        user.save()

    except User.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    return HttpResponse({}, content_type='application/json', status=200)


def trim_zeros(party_id):
    while party_id[0] == '0':
        party_id = party_id[1:]

    return party_id