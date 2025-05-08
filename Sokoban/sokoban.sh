#!/bin/bash
if [ $# -lt 1 ] || [ $# -gt 2 ]; then
    echo "Usage : $0 <numero_niveau> [timeout]"
    exit 1
fi

NIVEAU="$1"
TIMEOUT="${2:-600}"

if [ ! -f parseProblem.py ]; then
    echo "Erreur : Le fichier parseProblem.py est introuvable."
    exit 2
fi

# Exécution du parsing et de la résolution du problème
python parseProblem.py "$NIVEAU" "$TIMEOUT"
CODE=$?
if [ $CODE -ne 0 ]; then
    echo "Erreur lors de l'exécution du parsing ou du planificateur (code $CODE)."
    exit $CODE
fi

# Vérifier que le plan a bien été généré
if [ ! -s plan.txt ]; then
    echo "Erreur : Aucun plan généré (plan.txt manquant ou vide)."
    exit 3
fi

echo "Plan généré avec succès pour le niveau $NIVEAU."
echo "Compilation Maven..."
mvn compile
MVN_CODE=$?
if [ $MVN_CODE -ne 0 ]; then
    echo "Erreur lors de la compilation Maven (code $MVN_CODE)."
    exit $MVN_CODE
fi

echo "Lancement du jeu Sokoban niveau $NIVEAU..."
java --add-opens java.base/java.lang=ALL-UNNAMED \
    -server -Xms2048m -Xmx2048m \
    -cp "$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q):target/test-classes/:target/classes" \
    sokoban.SokobanMain "$NIVEAU"
JAVA_CODE=$?
if [ $JAVA_CODE -ne 0 ]; then
    echo "Erreur lors de l'exécution du jeu (code $JAVA_CODE)."
    exit $JAVA_CODE
fi

exit 0