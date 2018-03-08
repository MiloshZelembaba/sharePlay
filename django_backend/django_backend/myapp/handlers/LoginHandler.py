from django.http import HttpResponse


def passOff(request):
    return HttpResponse("herro")
