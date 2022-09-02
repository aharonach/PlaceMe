import RecordDetails from "../../RecordDetails";
import {useOutletContext} from "react-router-dom";
import Classes from "./Classes";
import {boolToString} from "../../../utils";
import Checkmark from "../../General/Checkmark";

export default function ResultData({ externalResult }){
    let { result } = useOutletContext();
    result = externalResult ?? result;

    const details = [
        { label: "Name", value: result.name },
        { label: "Description", value: result.description },
        { label: "Created Time", value: result.createdTime },
        { label: "Is Selected Result", value: <Checkmark checked={result.selected}>{boolToString(result.selected)}</Checkmark> },
        { label: "Number Of Classes", value: result.numberOfClasses },
        { label: "Score", value: result.placementScore },
    ];

    return (
        <>
            <RecordDetails details={details} />
            <Classes result={result} />
        </>
    );
}