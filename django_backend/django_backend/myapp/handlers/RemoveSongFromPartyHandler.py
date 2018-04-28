from threading import Thread

from django.http import HttpResponse
import json
from myapp.models import Party
from myapp.models import Song
from ClientRequests import NotifyPartyUpdated


def passOff(json_data):
    song_id = json_data['song']['id']

    party = None
    try:
        song = Song.objects.get(id=song_id)
        party = song.party
        song.delete()
    except Song.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    response = {}
    response['party'] = party.to_dict(addSongs=True)

    # the thread implementation below is how i should be doing it
    # thread = Thread(target=send_update, args=(party))
    # thread.start()
    try:
        send_update(party)
        return HttpResponse(json.dumps(response, indent=4, sort_keys=True, default=str), content_type='application/json',
                            status=200)
    except Exception:
        return HttpResponse("Error sending party update to clients", content_type='application/json', status=418)


def send_update(party):
    try:
        NotifyPartyUpdated.run(party)
    except Exception:
        return HttpResponse("Error sending party update to clients", content_type='application/json', status=418)
