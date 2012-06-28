#!/usr/bin/env python
# -*- encoding: utf-8

# for giving random response
from random import randint
import pickle

# sample space
w = ("K", "P", "S")

# learning window size, that is how many items from the history should we
# consider. The longer the queue is, the better we can find out user tendensies
# but it also requires more history to be accurate
window_size = 5

# dict for easily seeking how many times, n, the sample queue
# w1,w2..w_windowsize-1 has been followed by w_windowsize. Keys are in form
# "w1w2w3..w_windosize" : n
freq_x = {}

# the history of user selections
history = ""

# game statistics
cpu_wins = 0
player_wins = 0
ties = 0


def main():
    global history
    global freq_x
    global w
    global cpu_wins
    global player_wins
    global ties

    # try to unpickle data

    try:
        # open file and load data
        with open("rpsdata.pk1", "rb") as pkfile:
            data = pickle.load(pkfile)
        #    data = (history, cpu_wins, player_wins, ties)
        history = data[0]
        cpu_wins = data[1]
        player_wins = data[2]
        ties = data[3]
        rebuild_freqs(history)
    except IOError:
        # if there was io error, file most likely didn't exist. nonetheless,
        # we don't really care
        pass
    except Exception, e:
        print e

    print "Pelataan!"

    print_help()

    while (True):
        wi = get_input()
        if wi == "L":
            break
        elif wi == "T":
            print_stats()
            continue

        # update the history
        history = history + wi

        r = select_response()
        update_freqs()
        calculate_winner(wi, r)

    # after the game loop, pickle and save the data
    data = (history, cpu_wins, player_wins, ties)
    output = open("rpsdata.pk1", "wb")
    pickle.dump(data, output)
    output.close()


def rebuild_freqs(rebuildlist):
    global freq_x
    print "rebuilding freqs"
    for i in range(len(rebuildlist)):
        feature = rebuildlist[i:i + window_size]
        if len(feature) < window_size:
            break
        elif feature in freq_x:
            freq_x[feature] = freq_x[feature] + 1
        else:
            freq_x[feature] = 1
    print "done, freqs"
    print freq_x


def print_stats():
    total = cpu_wins + player_wins + ties + 0.00001 # avoid divbyzero
    player_percentage = player_wins * 100.0 / total
    cpu_percentage = cpu_wins * 100.0 / total
    tie_percentage = ties * 100.0 / total
    print "Pelejä: %1d " % total
    print "Pelaaja voittanut: %1d (%2d%%)" % (player_wins, player_percentage)
    print "CPU voittanut: %1d (%2d%%)" % (cpu_wins, cpu_percentage)
    print "Tasapelejä: %1d (%2d%%)" % (ties, tie_percentage)
    print "Historia: " + history


def calculate_winner(your, mine):
    winner = None
    global cpu_wins
    global player_wins
    global ties

    if ((your == 'K' and mine == 'S') or
    (your == 'P' and mine == 'K') or
    (your == 'S' and mine == 'P')):
        winner = "You"
        player_wins = player_wins + 1

    elif ((your == 'K' and mine == 'P') or
    (your == 'P' and mine == 'S') or
    (your == 'S' and mine == 'K')):
        winner = "I"
        cpu_wins = cpu_wins + 1
    else:
        ties = ties + 1

    print " ".join(["Your", your, "against", "my", mine])

    if winner:
        print winner + " won!"
    else:
        print "It's a tie!"


def select_response():
    global freq_x
    global history

    # at first rounds pick the response just randomly, there isn't enough
    # history data to make statistical choice
    response = ""
    feature = history[-window_size:]
    if len(feature) != window_size:
        print "random response, small history"
        response = w[randint(0, 2)]
        return response

    # simple logic: pick the one response that wins the input player will
    # statistically pick
    fk = 0
    fp = 0
    fs = 0
    if feature[:window_size - 1] + "K" in freq_x:
        fk = freq_x[feature[:window_size - 1] + "K"]
    if feature[:window_size - 1] + "P" in freq_x:
        fp = freq_x[feature[:window_size - 1] + "P"]
    if feature[:window_size - 1] + "S" in freq_x:
        fs = freq_x[feature[:window_size - 1] + "S"]

    if fk == fp == fs:
        print "random response, equal chance"
        response = w[randint(0, 2)]
    elif (fk >= fp and fk >= fs):
        response = "P"
    elif (fp >= fk and fp >= fs):
        response = "S"
    else:
        response = "K"

    return response


def get_input():
    wi = raw_input("> ").upper()
    while (wi not in w) and wi not in ['L', 'T']:
        print_help()
        wi = raw_input("> ").upper()

    return wi


def print_help():
    print "Anna syötteenä (K)ivi, (P)aperi tai (S)akset."
    print "(L) lopettaa pelin. (T) näyttää tilastot"


def update_freqs():
    global history
    global freq_x

    # if we don't have enough history, there's nothing to update
    if len(history) < window_size:
        return

    # otherwise, update freqs
    feature = history[-window_size:]
    if feature in freq_x:
        freq_x[feature] = freq_x[feature] + 1
    else:
        freq_x[feature] = 1


if __name__ == "__main__":
    main()
