import RecordDetails from "../RecordDetails";
import {Link, useOutletContext} from "react-router-dom";
import ResultData from "./Results/ResultData";
import GenerateFirstResults from "./GenerateFirstResults";

export default function PlacementData(){
    const { placement } = useOutletContext();
    const details = [
        { label: "Number Of Classes", value: placement.numberOfClasses },
        { label: "Group", value: placement.groupId
                ? placement.group.name
                : <Link to="edit">Assign to a group</Link>},
        { label: "Created On", value: placement.createdTime },
    ];

    console.log(placement?.selectedResult);

    const selectedResult = <>
        {placement?.selectedResult ? (
            <>
                <hr />
                <h3>Selected Result</h3>
                <ResultData externalResult={placement.selectedResult} />
            </>
        ) : <GenerateFirstResults hasResults />}
    </>;

    return (
        <>
            <RecordDetails details={details} />
            {!placement?.numberOfResults
                ? <GenerateFirstResults /> : selectedResult}
        </>
    );
}