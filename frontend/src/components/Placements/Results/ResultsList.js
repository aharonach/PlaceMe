import {Link, useOutletContext} from "react-router-dom";
import columns from "./columns";
import RecordList from "../../RecordList";
import Checkmark from "../../General/Checkmark";
import {useState} from "react";
import Status from "./Status";
import {extractListFromAPI, fixedNumber} from "../../../utils";
import GenerateFirstResults from "../GenerateFirstResults";

export const isCompleted = (status) => {
    return status === 'COMPLETED';
}

export default function ResultsList(){
    const { placement } = useOutletContext();
    const [updatedList, setUpdateList] = useState(0);

    const updateList = () => {
        setUpdateList(updatedList + 1);
    }

    const mapResults = ( result ) => {
        return { ...result,
            name: isCompleted(result.status) ? <Link to={`/placements/${placement.id}/results/${result.id}`}>{result.name}</Link> : result.name,
            placementScore: fixedNumber(result.placementScore),
            status: <Status placementResult={result} />,
            selected: <Checkmark checked={result.selected} />
        }
    };

    const checkResultsStatus = (response) => {
        const results = extractListFromAPI(response, 'placementResultList');
        const inProgressResult = results.find( result => result.status === 'IN_PROGRESS' );

        if ( inProgressResult ) {
            setTimeout(() => updateList(), 1000);
        }
    }

    return (
        <>
            <RecordList
                fetchUrl={`/placements/${placement.id}/results`}
                propertyName="placementResultList"
                title={<h2>Optional Results</h2>}
                addButton="Generate Result"
                columns={columns}
                mapCallback={mapResults}
                thenCallback={checkResultsStatus}
                updated={updatedList}
                hero={<GenerateFirstResults />}
            />
        </>
    );
}
