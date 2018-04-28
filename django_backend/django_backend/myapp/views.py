from django.http import HttpResponse

from models import User
from handlers import LoginHandler
from handlers import CreatePartyHandler
from handlers import JoinPartyHandler
from handlers import AddSongToPartyHandler
from handlers import GetPartyDetailsHandler
from handlers import IncrementSongVoteCountHandler
from handlers import UpdateNetworkInfo
from handlers import LeavePartyHandler
from handlers import RemoveSongFromPartyHandler
from handlers import UpdateNetworkTemporary
import json

def login(request):
    if request.method == "POST":
        received_json_data = json.loads(request.body.decode("utf-8"))
        return LoginHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def joinParty(request):
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return JoinPartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def leaveParty(request):
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return LeavePartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def createParty(request):
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return CreatePartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def addSongToParty(request):
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return AddSongToPartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def removeSongFromParty(request):
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return RemoveSongFromPartyHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def getPartyDetails(request):
    # TODO: i could make this one a get but i mean does it really matter?
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return GetPartyDetailsHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")


def incrementSongVoteCount(request):
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return IncrementSongVoteCountHandler.passOff(received_json_data)
    else:
        return HttpResponse("poop")

def updateNetworkInfo(request):
    if request.method == "POST":
        UpdateNetworkTemporary.updateNetworkInfo(request)
        received_json_data = json.loads(request.body.decode("utf-8"))
        return UpdateNetworkInfo.passOff(received_json_data)
    else:
        return HttpResponse("poop")


def getEmailAddress(request):
    milosh = User.objects.get(first_name="Milosh")
    return HttpResponse(milosh.email)


def initMilosh(request):
    milosh = User(first_name="milosh", last_name="zelembaba", email="miloshzelembaba@gmail.com", password="fire", address="nothing yet", port=0)
    milosh.save()
    return HttpResponse("Success")

    # milosh = User(first_name="mike", last_name="dantoni", email="mikedantoni@gmail.com", password="fire")
    # milosh.save()
