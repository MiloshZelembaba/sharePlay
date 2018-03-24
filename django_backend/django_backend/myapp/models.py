from django.db import models

class User(models.Model):
    id = models.AutoField(primary_key=True)
    first_name = models.CharField(max_length=30, null=False)
    last_name = models.CharField(max_length=30, null=False)
    email = models.CharField(max_length=50, null=False)
    password = models.CharField(max_length=30, null=False)
    last_login = models.DateField(null=True)
    address = models.CharField(max_length=100, null=False)
    port = models.IntegerField(null=False)
    current_party = models.ForeignKey(
                'Party',
                null=True)

    def to_dict(self):
        _dict = {}
        _dict['id'] = self.id
        _dict["first_name"] = self.first_name
        _dict["last_name"] = self.last_name
        _dict["email"] = self.email
        _dict["last_login"] = self.last_login
        #_dict["current_party"] = self.current_party

        return _dict

class Party(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=30, null=False)
    host = models.ForeignKey(
                'User',
                null=False,
                on_delete=models.CASCADE)

    def get_songs(self):
        songs = Song.objects.filter(party_id=self.id)
        songs = [song.to_dict() for song in songs]

        return songs

    def to_dict(self):
        _dict = {}
        _dict['id'] = self.id
        _dict['name'] = self.name
        _dict['host'] = self.host.to_dict()

        return _dict

class Song(models.Model):
    id = models.AutoField(primary_key=True)
    spotify_uri = models.CharField(max_length=100, null=False)
    song_name = models.CharField(max_length=100, null=False)
    artists = models.CharField(max_length=100, null=False)
    image_url = models.CharField(max_length=1000, null=False)
    party = models.ForeignKey(
                'Party',
                null=False,
                on_delete=models.CASCADE)
    vote_count = models.IntegerField()

    def to_dict(self):
        _dict = {}
        _dict['id'] = self.id
        _dict['uri'] = self.spotify_uri
        _dict['song_name'] = self.song_name
        _dict['artists'] = self.artists
        _dict['image_url'] = self.image_url
        _dict['vote_count'] = self.vote_count

        return _dict