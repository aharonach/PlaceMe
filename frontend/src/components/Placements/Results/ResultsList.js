import {useOutletContext} from "react-router-dom";
import columns from "./columns";
import RecordList from "../../RecordList";

export default function ResultsList(){
    const { placement } = useOutletContext();

    return (
        <RecordList
            fetchUrl={`/placements/${placement.id}/results`}
            propertyName="placementResultList"
            title={<h2>Optional Results</h2>}
            columns={columns}
            linkField="name"
            addButton={"Generate Result"}
        />
    );
}
