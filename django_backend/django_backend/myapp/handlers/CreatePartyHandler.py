from django.http import HttpResponse
import json
from myapp.models import User
from myapp.models import Party


# TODO: DB look ups are case sensitive, should store all lowercase or whatever
def passOff(json_data):
    user_id = json_data['user']['id']
    party_name = json_data['party_name']

    user = None
    unique_code = generate_unique_code()

    try:  # checks that no party with that code exists
        Party.objects.get(unique_code=unique_code)
        return HttpResponse("Party with code already exists", content_type='application/json', status=418)
    except Party.DoesNotExist:
        pass

    try:
        user = User.objects.get(id=user_id)
        party = Party(name=party_name, unique_code=unique_code, host=user)
        party.save()
        # need to get here so that we get the ID
        party = Party.objects.get(unique_code=unique_code)
        user.current_party = party
        user.save()
    except User.DoesNotExist, Party.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    data = {}
    data['party_id'] = party.id
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json', status=200)


def generate_unique_code():
    return "000033"