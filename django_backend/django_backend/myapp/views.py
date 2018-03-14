from django.http import HttpResponse
from models import User
from handlers import LoginHandler
from handlers import CreatePartyHandler
from handlers import JoinPartyHandler
from handlers import AddSongToPartyHandler
from handlers import GetPartyDetailsHandler
import json

def login(request):
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return LoginHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def joinParty(request):
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return JoinPartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def createParty(request):
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return CreatePartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def addSongToParty(request):
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return AddSongToPartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def getPartyDetails(request):
    # TODO: i could make this one a get but i mean does it really matter?
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return GetPartyDetailsHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")


def getEmailAddress(request):
    milosh = User.objects.get(first_name="Milosh")
    return HttpResponse(milosh.email)


def initMilosh(request):
    milosh = User(first_name="mike", last_name="dantoni", email="mikedantoni@gmail.com", password="fire")
    milosh.save()
    return HttpResponse("Success")