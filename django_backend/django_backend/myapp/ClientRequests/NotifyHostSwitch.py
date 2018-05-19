import json
from myapp.models import User
from request import send_request
from threading import Thread

def run(user):
    try:
        data = {}
        data['type'] = 'host_switch'
        data['party_id'] = user.current_party_id

        thread = Thread(target=send_request, args=(data, user.address, user.port))
        thread.start()

    except Exception:
        return HttpResponse("Error sending host switch to client", content_type='application/json', status=418)