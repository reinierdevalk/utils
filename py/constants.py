# 1. PARSER VALUES
INPUT = 'i'

# --tuning
# NB Must be the same as in representations.external.Tablature
D = 'D'
E = 'E'
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

# --placement
TOP = 'top'
BOTTOM = 'bottom'

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


# 2. OTHER
NOTATIONTYPES = {FLT: 'tab.lute.french',
				 ILT: 'tab.lute.italian',
				 SLT: 'tab.lute.spanish',
				 GLT: 'tab.lute.german'
				}
