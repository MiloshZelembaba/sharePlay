from django.http import HttpResponse
import json
from myapp.models import Song
from myapp.models import Party


# TODO: DB look ups are case sensitive, should store all lowercase or whatever
def passOff(json_data):
    song_id = json_data['song']['id']

    try:
        song = Song.objects.get(id=song_id)
        song.vote_count += 1
        song.save()
        party = Party.objects.get(id=song.party.id)
    except Song.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    data = {}
    data['party'] = party.to_dict()
    data['songs'] = party.get_songs()
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json',
                        status=200)