from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'django_backend.views.home', name='home'),
    # url(r'^django_backend/', include('django_backend.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
    url(r'^login/', 'myapp.views.login'),
    url(r'^joinParty/', 'myapp.views.joinParty'),
    url(r'^createParty/', 'myapp.views.createParty'),
    url(r'^addSongToParty/', 'myapp.views.addSongToParty'),
    url(r'^getPartyDetails/', 'myapp.views.getPartyDetails'),
    url(r'^initMilosh/', 'myapp.views.initMilosh'),

)
