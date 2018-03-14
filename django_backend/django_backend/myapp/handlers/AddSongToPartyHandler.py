from django.http import HttpResponse
import json
from myapp.models import User
from myapp.models import Party
from myapp.models import Song


def passOff(json_data):
    user_id = json_data['user']['id']
    party_id = json_data['party']['id']
    uri = json_data['song']['uri']

    # verify song doesn't already exist in the party
    try:
        party = Party.objects.get(id=party_id) ## dangerous to assume
        song = Song.objects.get(spotify_uri=uri, party=party)
        return HttpResponse("Party with code already exists", content_type='application/json', status=418)
    except Song.DoesNotExist:
        pass

    # add song to party
    try:
        user = User.objects.get(id=user_id)
        party = Party.objects.get(id=party_id)

        if (user.current_party != party):
            return HttpResponse("Can't add songs to a party your not in", content_type='application/json', status=418)

        song = Song(spotify_uri=uri, party=party, vote_count=1)
        song.save()
    except User.DoesNotExist, Party.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    # TODO: need to broadcast an update here to everyone in tha party
    data = {}
    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json', status=200)