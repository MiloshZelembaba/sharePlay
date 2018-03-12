from django.db import models

class User(models.Model):
    first_name = models.CharField(max_length=30, null=False)
    last_name = models.CharField(max_length=30, null=False)
    email = models.CharField(max_length=50, null=False)
    password = models.CharField(max_length=30, null=False)
    last_login = models.DateField(null=True)
    current_party = models.ForeignKey(
                'Party',
                null=True,
                on_delete=models.CASCADE)

    def to_dict(self):
        _dict = {}
        _dict["first_name"] = self.first_name
        _dict["last_name"] = self.last_name
        _dict["email"] = self.email
        _dict["last_login"] = self.last_login
        #_dict["current_party"] = self.current_party

        return _dict


class Party(models.Model):
    name = models.CharField(max_length=30, null=False)
    unique_code = models.CharField(max_length=6, null=False)
    host = models.ForeignKey(
                'User',
                null=False,
                on_delete=models.CASCADE) ## TODO: MAJOR - DELET THE CASCADE

    def to_dict(self):
        _dict = {}
        _dict['name'] = self.name
        _dict['code'] = self.unique_code
        _dict['host'] = self.host.to_dict()

        return _dict

class Song(models.Model):
    spotify_uri = models.CharField(max_length=100, null=False)
    party = models.ForeignKey(
                'Party',
                null=False,
                on_delete=models.CASCADE)
    vote_count = models.IntegerField()
