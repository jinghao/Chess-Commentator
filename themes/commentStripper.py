import glob
import re

for f in glob.glob('*/*.pgn'):
    content = open(f, 'r').read()
    # Strip comments
    open(f + '-fixed', 'w').write(re.sub(r'\{(.|\n)*?\}', '', content))

    
