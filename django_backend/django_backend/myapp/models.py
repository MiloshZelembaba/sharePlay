from django.db import models

class User(models.Model):
    first_name = models.CharField(max_length=30)
    last_name = models.CharField(max_length=30)
    email = models.CharField(max_length=50)
    current_party = models.ForeignKey(
                'Party',
                null=True,
                on_delete=models.CASCADE)

class Party(models.Model):
    name = models.CharField(max_length=30)
    host = models.ForeignKey(
                'User',
                on_delete=models.CASCADE)

class Song(models.Model):
    spotify_uri = models.CharField(max_length=100)
    party = models.ForeignKey(
                'Party',
                on_delete=models.CASCADE)
    vote_count = models.IntegerField()
