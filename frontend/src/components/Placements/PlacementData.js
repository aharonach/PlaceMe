import RecordDetails from "../RecordDetails";
import {Link, useOutletContext} from "react-router-dom";
import ResultData from "./Results/ResultData";
import GenerateFirstResults from "./GenerateFirstResults";
import {humanizeTime} from "../../utils";

export default function PlacementData(){
    const { placement } = useOutletContext();
    const details = [
        { label: "Number Of Classes", value: placement.numberOfClasses },
        { label: "Group", value: placement.groupId
                ? <Link to={`/groups/${placement.groupId}`}>{placement.group.name}</Link>
                : <Link to="edit">Assign to a group</Link>},
        { label: "Created On", value: humanizeTime(placement.createdTime) },
    ];

    const selectedResult = <>
        {!placement?.selectedResult ? <GenerateFirstResults hasResults/> : <>
            <hr/>
            <h3>Selected Result</h3>
            <ResultData externalResult={placement.selectedResult}/>
        </>}
    </>;

    return (
        <>
            <RecordDetails details={details} />
            {placement?.numberOfResults ? selectedResult : <GenerateFirstResults/>}
        </>
    );
}