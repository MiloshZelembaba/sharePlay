from django.http import HttpResponse
import json
from myapp.models import User


def passOff(json_data):
    password = json_data['password']
    email = json_data['email']
    user = User.objects.get(email=email)

    import pdb; pdb.set_trace()


    data = {'user' : {
        'first_name' : 'Milosh',
        'last_name' : 'Zelembaba',
        },
        'is_logged_in' : True,
    }
    return HttpResponse(json.dumps(data), content_type='application/json')



