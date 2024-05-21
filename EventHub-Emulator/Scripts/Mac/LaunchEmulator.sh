#!/bin/bash

# Initialize variables
ACCEPT_EULA='n'
CONFIG_PATH='../EventHub-Emulator/Config/Config.json'
composeFile=$(realpath "$(dirname "$BASH_SOURCE")/../../../Docker-Compose-Template/docker-compose-default.yml")

# Loop through all arguments
for arg in "$@"
do
    if [[ $arg == --ACCEPT_EULA=* ]]; then
        ACCEPT_EULA="${arg#*=}"
        if [[ "$ACCEPT_EULA" != "y" && "$ACCEPT_EULA" != "Y" ]]; then
            # EULA
            echo 'You must accept the EULA (Pass --ACCEPT_EULA='Y' parameter to the script) to continue. Exiting script.'
            exit 1
        fi
    fi

    if [[ $arg == --CONFIG_PATH=* ]]; then
        CONFIG_PATH="${arg#*=}"
    fi
done

# Check if ACCEPT_EULA is 'Y' or 'y'
if [[ "$ACCEPT_EULA" != "y" && "$ACCEPT_EULA" != "Y" ]]; then
    # EULA
    echo 'By pressing "Y", you are expressing your consent to the End User License Agreement (EULA) for Event-Hubs Emulator: https://github.com/Azure/azure-event-hubs-emulator-installer/blob/main/EMULATOR_EULA.md'
    read ACCEPT_EULA
    if [[ "$ACCEPT_EULA" != "y" && "$ACCEPT_EULA" != "Y" ]]; then
        echo "You must accept the EULA (Press 'Y') to continue. Exiting script."
        exit 1
    fi
fi

# Set EULA as env variable
echo "EULA has been accepted. Proceeding with launching containers.."
export ACCEPT_EULA=$ACCEPT_EULA

# Set Config Path as env variable
export CONFIG_PATH=$CONFIG_PATH

docker compose -f $composeFile down
docker compose -f $composeFile up -d

if [ $? -ne 0 ]; then
    echo "An error occurred while running docker compose.Exiting the script."
    exit 1
fi

echo "Emulator Service and dependencies have been successfully launched!"