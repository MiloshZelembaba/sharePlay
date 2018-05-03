from django.http import HttpResponse
import json
from myapp.models import User


def passOff(json_data):
    email = json_data['email']
    display_name = json_data['display_name']

    try:
        user = User.objects.get(email=email)
        user.current_party_id = None
        user.save()
        data = user.to_dict()
    except User.DoesNotExist:
        new_user = User(display_name=display_name, email=email, address="nothing yet", port=0)
        new_user.save()
        data = new_user.to_dict()

    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json')



