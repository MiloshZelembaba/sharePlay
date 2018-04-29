from django.http import HttpResponse
import json
from myapp.models import User


def passOff(json_data):
    email = json_data['email']
    first_name = "tmp"
    last_name = "tmp"

    try:
        user = User.objects.get(email=email)
        data = user.to_dict()
    except User.DoesNotExist:
        new_user = User(first_name=first_name, last_name=last_name, email=email,
                      password="fire", address="nothing yet", port=0)
        new_user.save()
        data = new_user.to_dict()

    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json')



