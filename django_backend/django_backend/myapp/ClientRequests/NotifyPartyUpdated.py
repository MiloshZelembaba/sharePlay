import json
from myapp.models import User
from request import send_request
from threading import Thread

def run(party):
    try:
        users = User.objects.filter(current_party=party)
        data = {}
        data['party'] = party.to_dict(addSongs=True)
        data['type'] = 'update_party'

        for user in users:
            thread = Thread(target=send_request, args=(data, user.address, user.port))
            thread.start()
    except Exception:
        return HttpResponse("Error sending party update to clients", content_type='application/json', status=418)