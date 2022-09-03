import React from 'react';
import RecordList from "../RecordList";
import columns from "./columns";
import {toCapitalCase} from "../../utils";
import Gender from "../General/Gender";

export default function PupilsList() {
    const mapResults = ( pupil ) => {
        return { ...pupil,
            gender: <Gender gender={pupil.gender} noIcon pill>{toCapitalCase(pupil.gender)}</Gender>
        }
    };

    return <RecordList
        fetchUrl="/pupils/"
        propertyName="pupilList"
        title={<h1>Pupils</h1>}
        addButton="Add Pupil"
        columns={columns}
        mapCallback={mapResults}
        linkField="givenId"
        sorting={['firstName', 'lastName', 'givenId', 'createdTime', 'gender', 'birthDate']}
    />
}
