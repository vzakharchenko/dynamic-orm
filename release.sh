set -e

PROPERTY_FILE=./release.properties

function help() {
  echo '
Usage release.sh OPTIONS
create release
Options:
       --help                         Help screen
       --password <pgp password>      pgp password
'
}

POSITIONAL=()
while [[ $# -gt 0 ]]; do
  key="$1"

  case $key in
  --password)
    password="$2"
    shift
    shift
    ;;
  --help)
    help
    exit
    ;;
  *) # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift              # past argument
    ;;
  esac
done

set -- "${POSITIONAL[@]}" # restore positional parameters

if [[ "x${password}" == "x" ]]; then
  echo "Type pgp password:"
  read password
fi

if [[ "x${password}" == "x" ]]; then
  echo "Password is empty"
  exit 1;
fi

# prepare release
mvn clean release:prepare -Psign -Darguments=-Dgpg.passphrase=${password} -Dresume=false
# get release tag name
tagName=`cat $PROPERTY_FILE | grep "scm.tag" | grep -i -v -E "scm.tagNameFormat" | cut -d'=' -f2`
# get release version
tagVersion=`cat $PROPERTY_FILE | grep "project.rel.com.github.vzakharchenko..dynamic-orm"  | cut -d'=' -f2`
tagDevVersion=`cat $PROPERTY_FILE | grep "project.dev.com.github.vzakharchenko..dynamic-orm"  | cut -d'=' -f2`

if [[ "x${tagVersion}" == "x" ]]; then
  echo "tagVersion is empty"
  exit 1;
fi

if [[ "x${tagName}" == "x" ]]; then
  echo "tagName is empty"
  exit 1;
fi

# get perform release
mvn -Psign clean release:perform -Darguments=-Dgpg.passphrase=${password}

# create release
git pull
hub release create -m "Dynamic Orm ${tagName}" $tagName


