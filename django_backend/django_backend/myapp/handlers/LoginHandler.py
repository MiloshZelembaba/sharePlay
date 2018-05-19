from django.http import HttpResponse
import json
from myapp.models import User


def passOff(json_data):
    email = json_data['email']
    display_name = json_data['display_name']
    product_flavour = json_data['product']

    try:
        user = User.objects.get(email=email)
        user.current_party_id = None
        user.product = product_flavour
        user.save()
        data = user.to_dict()
    except User.DoesNotExist:
        new_user = User(display_name=display_name, email=email, address="nothing yet", port=0, product=product_flavour)
        new_user.save()
        data = new_user.to_dict()

    return HttpResponse(json.dumps(data, indent=4, sort_keys=True, default=str), content_type='application/json')



