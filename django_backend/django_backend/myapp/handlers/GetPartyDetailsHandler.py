from django.http import HttpResponse
import json
from myapp.models import Party


def passOff(json_data):
    party_id = json_data['party_id']

    party = None
    try:
        party = Party.objects.get(id=party_id)
    except Party.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    data = {}
    data['party'] = party.to_dict()
    data['songs'] = party.get_songs()
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json', status=200)