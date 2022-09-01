import RecordDetails from "../../RecordDetails";
import {useOutletContext} from "react-router-dom";
import Classes from "./Classes";

export default function ResultData(){
    const { result } = useOutletContext();

    const details = [
        { label: "Name", value: result.name },
        { label: "Description", value: result.description },
        { label: "Created Time", value: result.createdTime },
        { label: "Is Selected Result", value: String(result.selected) },
        { label: "Number Of Classes", value: result.numberOfClasses },
        { label: "Score", value: result.placementScore },
    ];

    return (
        <>
            <RecordDetails details={details} />
            <Classes />
        </>
    );
}