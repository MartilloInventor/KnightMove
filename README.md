# KnightMove

_The Problem_

__Knight Sequences__

Pictured below is a keypad:

![Image](dialpad.png)

We want to find all 10-key sequences that can be keyed into the keypad in the following manner:

    • The initial keypress can be any of the keys.
    • Each subsequent keypress must be a knight move from the previous keypress.  
    • There can be at most 2 vowels in the sequence.

A knight move is made in one of the following ways:

    1. Move two steps horizontally and one step vertically.
    2. Move two steps vertically and one step horizontally.
    
There is no wrapping allowed on a knight move.

Below are some examples of knight moves:

![Image](./dialpadmoves.png)

Your program should first write the number of 
valid 10-key sequences on a single line to standard 
out.

_Digression_

It is worth to review Java at this point with a
slightly more complex problem.

AI researchers (especially those programming computers)
to play games of various types of searches in order
to evaluate positions.

If I remember correctly, Patrick Winston's book
entitled [Artificial Intelligence](https://books.google.com/books/about/Artificial_Intelligence.html?id=b4owngEACAAJ),
which I used to teach an AI course, discusses depth
first and breadth first searches.

For example, in chess playing breadth first is useful
because there is no reason to descend to a depth of
40 plies if a given ply guarantees mate in 2 plies. 
(Be precise, and use [ply](https://en.wikipedia.org/wiki/Ply_(game_theory)) instead of move
or turn!)

Yet depth first is also useful to find good plies
when no ply leads quickly to a quick one.

If I remember correctly, Deep Blue required a ply
depth of a least 40 to defeat Kasparov some of the
time, and it could not always do so.

Deep Blue and similar chess computers do not seem to
play chess as humans do. 

Current artificial intelligence
(sometimes called AI-lite) focuses on huge databases.

More traditional artificial intelligence focuses on
algorithmic analysis. Deep learning mostly combines
AI-lite with some form of pattern recognizer -- generally
a [neural network](https://www.technologyreview.com/s/513696/deep-learning/).

This sort of AI and deep learning is interesting but
almost certainly completely unrelated to natural
intelligence, which seems to be based in quantum computation
by biorganic membrane structures, which certainly don't
have the speed and (probably not) the memory capacity
of modern binary silicon based computers.

_Back to the Problem_

The solution is contained in the following directory.

    KnightMove/src/main/java/edu/desktop/knightmove/KnightMove.java

I assume wrap means no key appears twice in a 
sequence. Thus sequences like:

    1. CHMNIDCHMN, 
    2. CHMNIDCHML, 
    3. CHMLMN321L, or
    4. CHMLBGAFGL

are disallowed.

If that definition is too strict, it is a minor
change in the program by imposing different
constraints on a single move or sequence of moves.

The constraints are applied in the complete motion
vertHorz() and single step getStep[123]() routines.

I tried to factor the program reasonably and
comprehensibly.

Although the statement of the problem does not
require a window-based interface, I gave it one
because I don't like to interleave program output
with debugger output. This simple window interface
works for Scala (uggh!) and Clojure. I have another
one for C and C++. I need to develop one for
Python/Jython/Cython, JavaScript 
(front-end in a web browser and back-end in Node.js)
intrinsically uses popups.

I like to use Java enum because it facilitates
range checking and is helpful in building 
command interpreters (a functionality not used
in the program). An enum is a sort of single 
instance object.

Note how I use static and non-static methods.

I have vowed to make more and better use of
[Java access control](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html).

Knightmove/pom.xml is almost an example of a simple pom.xml for
building a simple desktop application with
[Maven](https://maven.apache.org/).

It uses a shaded jar, which is explained in this
Gradle web page entitled [Shading -- ForgeGradle](https://forgegradle.readthedocs.io/en/latest/user-guide/shading/). 

If you are developing SAAS and the output may go to
an Android smartphone, you probably need to learn Gradle 
because Android development has standardized on
[Gradle](https://gradle.org/) instead of 
[Maven](https://maven.apache.org/) as
a [build tool](https://technologyconversations.com/2014/06/18/build-tools/)
The Minecraft API seems to be build with Gradle.

I put a short test shell script in the top level
KnightMove directory. 
It's name is repeatknightmove.sh.

Only the following line is interesting.

`trap "exit" INT`

This line is necessary so that Control-C can
terminate the script.
