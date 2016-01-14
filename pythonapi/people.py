from stolen_api import api

class Person(object):
    """ Represents a person in stolen API """

    def __init__(self, data):
        self.data = data

    @property
    def id(self):
        return self.data['id']

    # Twitter related fields
    @property
    def username(self):
        return self.data['identities'][0]['username']

    @property
    def user_id(self):
        return self.data['identities'][0]['user_id']

    @property
    def follower_count(self):
        return self.data['identities'][0]['follower_count']

    @property
    def friend_count(self):
        return self.data['identities'][0]['friend_count']

    @property
    def verified(self):
        return self.data['verified']

    # Price related fields
    @property
    def display_price(self):
        return self.data['lastsale']['display_price']

    @property
    def purchase_uuid(self):
        return self.data['lastsale']['purchase_uuid']

    @property
    def owner_id(self):
        return self.data['lastsale']['owner_id']

    @property
    def seconds_since_harvest(self):
        return self.data['lastsale']['seconds_since_harvest']

    @property
    def seconds_since_sale(self):
        return self.data['lastsale']['seconds_since_sale']

    @property
    def total_times_purchased(self):
        return self.data['lastsale']['total_times_purchased']

    @property
    def new_worth(self):
        return self.data['lastsale']['new_worth']

    @property
    def current_harvest_amount(self):
        return self.data['lastsale']['current_harvest_amount']

    @property
    def decay_rate(self):
        return self.data['lastsale']['decay_rate']

    @property
    def owner(self):
        if self.data.get('owner'):
            return Person(self.data.get('owner'))
        return Person.from_id(self.data['lastsale']['owner_id'])

    @property
    def balance(self):
        return self.data['wallet']['balance']

    @property
    def pet_slots(self):
        return self.data['pet_slots']

    def owns(self, person):
        return self.id.lower() == person.owner_id.lower()

    @classmethod
    def from_id(id):
        data = api.get_person(id)
        return Person(data)
