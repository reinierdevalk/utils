# 1. PARSER VALUES
INPUT = 'i'

# --tuning
# NB Must be the same as in representations.external.Tablature
F = 'F'
F6Eb = 'F6Eb'
G5 = 'G5'
G = 'G'
G6F = 'G6F'
A = 'A'
A6G = 'A6G'

# --mode
MAJOR = '0'
MINOR = '1'

# --score
SINGLE = 's'
DOUBLE = 'd'
VOCAL = 'v'

# --tablature
YES = 'y'
NO = 'n'

# --type
# NB Must be the same as in formats.tbp.symbols.TabSymbol
FLT = 'FLT'
ILT = 'ILT'
SLT = 'SLT'
GLT = 'GLT'

MEI = '.mei'
ASCII = '.tab'
TBP = '.tbp'
TC = '.tc'
XML = '.xml'

ALLOWED_FILE_FORMATS = [MEI, ASCII, TBP, TC, XML]
MARKUP_ELEMENTS = ['damage', 'unclear', 'del', 'add', 'supplied']


# 2. MEI
NOTATIONTYPES = {FLT: 'tab.lute.french',
				 ILT: 'tab.lute.italian',
				 SLT: 'tab.lute.spanish',
				 GLT: 'tab.lute.german'
				}

TUNINGS = {F   : [('f', 4), ('c', 4), ('g', 3), ('eb', 3), ('bb', 2), ('f', 2)],
		   F6Eb: [('f', 4), ('c', 4), ('g', 3), ('eb', 3), ('bb', 2), ('eb', 2)],
		   G5  : [('g', 4), ('d', 4), ('a', 3), ('f', 3), ('c', 3)],
		   G   : [('g', 4), ('d', 4), ('a', 3), ('f', 3), ('c', 3), ('g', 2)], 
		   G6F : [('g', 4), ('d', 4), ('a', 3), ('f', 3), ('c', 3), ('f', 2)], 
		   A   : [('a', 4), ('e', 4), ('b', 3), ('g', 3), ('d', 3), ('a', 2)], 
		   A6G : [('a', 4), ('e', 4), ('b', 3), ('g', 3), ('d', 3), ('g', 2)]
		  }

