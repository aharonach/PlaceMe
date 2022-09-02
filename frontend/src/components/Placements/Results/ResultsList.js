import {useOutletContext} from "react-router-dom";
import columns from "./columns";
import RecordList from "../../RecordList";
import Checkmark from "../../General/Checkmark";
import {useState} from "react";
import Status from "./Status";

export default function ResultsList(){
    const { placement } = useOutletContext();
    const [updatedList, setUpdateList] = useState(0);

    const updateList = () => {
        setUpdateList(updatedList + 1);
    };

    const mapResults = ( result ) => {
        return { ...result,
            placementScore: 'NaN' !== result.placementScore ? result.placementScore.toFixed(2) : '',
            status: <Status placementResult={result} updateList={updateList} />,
            selected: <Checkmark checked={result.selected} />
        }
    };

    return (
        <RecordList
            fetchUrl={`/placements/${placement.id}/results`}
            propertyName="placementResultList"
            title={<h2>Optional Results</h2>}
            columns={columns}
            linkField="name"
            addButton={"Generate Result"}
            mapCallback={mapResults}
            updated={updatedList}
        />
    );
}
