import time

from stolen_api import api
from errors import *
from people import Person
from predicates import *

NEW, TOP, FRIENDS, STOLEN = 'new', 'top', 'friends', 'stolen'

_FEEDS = {
    NEW: api.get_new,
    TOP: api.get_top,
    FRIENDS: api.get_friends,
    STOLEN: api.get_recently_stolen,
    
}

class SimpleStrategy(object):

    def __init__(self,
                 feed, halt_predicates,
                 trade_predicates,
                 trade_trial_counts=5,
                 buy_interval=3000,
                 call_interval=300):
        self.fetch_feed = _FEEDS[feed]
        self.trade_predicates = trade_predicates
        self.halt_predicates = halt_predicates
        self.me = api.get_me()
        self.my_id = api.get_my_id()
        self.buy_interval = buy_interval
        self.call_interval = call_interval
        self.trade_trial_counts = trade_trial_counts

    def _try_buy(self, person):
        trial_counts = 0
        while (trial_counts < self.trade_trial_counts):
            print "Trying to buy {} for: {}".format(person.username, person.display_price)
            me = Person(api.get_me())
            time.sleep(0.500)
            person = Person(api.get_person(person.id))
            if not all(p(person) for p in self.trade_predicates):
                break

            if any([p(me) for p in self.halt_predicates]):
                break
            time.sleep(0.200)
            if  me.balance > person.display_price:
                try:
                    api.buy(person.id, person.purchase_uuid)
                except InvalidRequest as ex:
                    trial_counts += 1
                    continue
                else:
                    break # Buy successful
            else:
                print "Display price {} higher than balance {}".format(person.display_price, me.balance)
                break
            trial_count += 1

    def execute(self):
        while True:
            me = Person(api.get_me())
            if any([p(me) for p in self.halt_predicates]):
                break
            time.sleep(0.500)
            feed = self.fetch_feed()
            for person_data in feed:
                person = Person(person_data)
                if all(p(person) for p in self.trade_predicates):
                    self._try_buy(person)

            time.sleep(self.buy_interval/1000)

_simple_strategy = SimpleStrategy(STOLEN, [has_balance], [is_verified, is_cheap])

if __name__ == '__main__':
    _simple_strategy.execute()

