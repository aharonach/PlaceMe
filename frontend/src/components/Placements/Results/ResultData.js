import React from 'react';
import RecordDetails from "../../RecordDetails";
import {useOutletContext} from "react-router-dom";
import Classes from "./Classes";
import {boolToString, humanizeTime} from "../../../utils";
import Checkmark from "../../General/Checkmark";
import Gender from "../../General/Gender";
import {PeopleFill} from "react-bootstrap-icons";
import {Stack} from "react-bootstrap";

export default function ResultData({ externalResult }){
    let { result } = useOutletContext();
    result = externalResult ?? result;

    const details = [
        // { label: "Name", value: result.name },
        { label: "Description", value: result.description },
        { label: "Is Selected Result", value: <Checkmark checked={result.selected}>{boolToString(result.selected)}</Checkmark> },
        { label: "Number Of Classes", value: result.numberOfClasses },
        { label: "Score", value: result.placementScore?.toFixed(2) },
        { label: "Number Of Pupils", value: (
            <Stack direction="horizontal" gap={2}>
                <Gender gender={"MALE"} >{result.totalNumberOfMales}</Gender>
                <Gender gender={"FEMALE"} >{result.totalNumberOfFemales}</Gender>
                <div><PeopleFill /> {result.group.numberOfPupils}</div>
            </Stack>
        )},
        { label: "Created Time", value: humanizeTime(result.createdTime) },
    ];

    return (
        <>
            <RecordDetails details={details} />
            <Classes result={result} />
        </>
    );
}