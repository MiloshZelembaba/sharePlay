from django.http import HttpResponse
from ClientRequests import NotifyHostSwitch
from myapp.models import User


def passOff(json_data):
    user_id = json_data['user']['id']

    user = None
    try:
        user = User.objects.get(id=user_id)
        party = user.current_party
        user.current_party = None
        user.save()

        if user.id is party.host_id:  # this means the host is leaving the party
            performPartySwitch(party, user)


    except User.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    return HttpResponse({}, content_type='application/json', status=200)


def trim_zeros(party_id):
    while party_id[0] == '0':
        party_id = party_id[1:]

    return party_id

def performPartySwitch(party, user):
    users_in_party = User.objects.filter(current_party=party)

    if len(users_in_party) is 0:
        ## TODO: might have to delete all of the songs in the party first
        party.delete()

    for user in users_in_party:
        if user.product in 'spotify_premium':
            party.host_id = user.id
            party.save()
            NotifyHostSwitch.run(user)
