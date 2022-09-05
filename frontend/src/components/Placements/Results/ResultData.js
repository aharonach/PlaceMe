import RecordDetails from "../../RecordDetails";
import {useOutletContext} from "react-router-dom";
import Classes from "./Classes";
import {boolToString, humanizeTime} from "../../../utils";
import Checkmark from "../../General/Checkmark";

export default function ResultData({ externalResult }){
    let { result } = useOutletContext();
    result = externalResult ?? result;

    const details = [
        { label: "Name", value: result.name },
        { label: "Description", value: result.description },
        { label: "Is Selected Result", value: <Checkmark checked={result.selected}>{boolToString(result.selected)}</Checkmark> },
        { label: "Number Of Classes", value: result.numberOfClasses },
        { label: "Score", value: result.placementScore },
        { label: "Created Time", value: humanizeTime(result.createdTime) },
    ];

    return (
        <>
            <RecordDetails details={details} />
            <Classes result={result} />
        </>
    );
}