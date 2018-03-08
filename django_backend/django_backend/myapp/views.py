from django.shortcuts import render
from django.http import HttpResponse
from scripts.oneoff.init import init_milosh_method
from models import User
from handlers import *

def login(request):
    return LoginHandler.passOff(request)


def getEmailAddress(request):
    milosh = User.objects.get(first_name="Milosh")
    return HttpResponse(milosh.email)
