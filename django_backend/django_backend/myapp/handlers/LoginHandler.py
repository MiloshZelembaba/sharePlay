from django.http import HttpResponse
import json
from myapp.models import User


def passOff(json_data):
    email = json_data['email']

    try:
        user = User.objects.get(email=email)
        data = user.to_dict()
    except User.DoesNotExist:
        return HttpResponse("Object does't exist", content_type='application/json', status=418)

    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json')



