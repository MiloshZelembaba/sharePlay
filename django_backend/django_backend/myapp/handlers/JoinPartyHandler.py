from django.http import HttpResponse
import json
from myapp.models import User
from myapp.models import Party


def passOff(json_data):
    user_id = json_data['user']['id']
    unique_code = json_data['code']

    user = None
    try:
        user = User.objects.get(id=user_id)
        party = Party.objects.get(unique_code=unique_code)
        user.current_party = party
        user.save()

    except User.DoesNotExist, Party.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    data = {}
    data['party_id'] = party.id
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json', status=200)