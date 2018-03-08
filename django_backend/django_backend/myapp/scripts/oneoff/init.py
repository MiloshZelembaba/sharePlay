from myapp.models import User

def init_milosh_method():
    # create some users
    milosh = User(first_name="Milosh",
                  last_name="Zelembaba",
                  email="miloshzelembaba@gmail.com",
                  current_party=None)
    milosh.save()
