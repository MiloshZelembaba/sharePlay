from django.http import HttpResponse
from models import User
from handlers import LoginHandler
from handlers import CreatePartyHandler
import json

def login(request):
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return LoginHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

# def joinParty(request):
#     if request.method == "POST":
#         received_json_data = json.loads(request.body.decode("utf-8"))
#         return LoginHandler.passOff(received_json_data)
#     else:
#         return HttpResponse("poop")

def createParty(request):
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return CreatePartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")



def getEmailAddress(request):
    milosh = User.objects.get(first_name="Milosh")
    return HttpResponse(milosh.email)
