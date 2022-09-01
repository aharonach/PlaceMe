import React, {useEffect, useState} from 'react';
import {
    Alert,
    Badge,
    Button,
    Card,
    Col,
    Row,
    Stack,
    ToggleButton,
    ToggleButtonGroup
} from "react-bootstrap";
import {useOutletContext} from "react-router-dom";
import useFetchList from "../../hooks/useFetchList";
import Loading from "../Loading";
import {objectIsEmpty} from "../../utils";
import Api from "../../api";
import { X } from 'react-bootstrap-icons';

export default function PreferencesInGroup() {
    const {group} = useOutletContext();
    const [pupils, loadingPupils, errorPupils] = useFetchList({ fetchUrl: `/groups/${group.id}/pupils`, propertyName: "pupilList" });
    const [preferences, errorPreferences, loadingPreferences] = useFetchList({ fetchUrl: `/groups/${group.id}/preferences`, propertyName: "preferenceList" });
    const [selector, setSelector] = useState(0);
    const [selected, setSelected] = useState(0);
    const [wantsToBe, setWantsToBe] = useState('yes');
    const [updateList, setUpdateList] = useState(false);

    const loadingFetch = loadingPupils || loadingPreferences;
    const errorFetch = errorPupils || errorPreferences;

    const reset = () => {
        setSelector(0);
        setSelected(0);
        setWantsToBe('yes');
    };

    const handleToggle = (value) => {
        setWantsToBe(value);
    }

    const handleSave = async () => {
        const response = await Api.put(`/groups/${group.id}/preferences`, prepareData(selector, selected, wantsToBe));

        if ( response.status === 200 ) {
            // @todo refresh the component
        }
    }

    return (
        <>
            <Loading show={loadingFetch} />
            {!loadingFetch && errorFetch && <Alert variant="danger">{errorFetch}</Alert> }
            {!loadingFetch && !errorFetch && pupils && preferences && (
                <>
                    <h3>Preferences</h3>
                    <Instructions selector={selector} />
                    <div className="mb-3">
                        <ToggleButtonGroup type="radio" name="wantsToBeWith" value={wantsToBe} onChange={handleToggle}>
                            <ToggleButton id="wantsToBeWith" value="yes" variant={buttonVariant(selector, wantsToBe, 'yes')} disabled={selector === 0}>Wants to be with</ToggleButton>
                            <ToggleButton id="doesntWantToBeWith" value="no" variant={buttonVariant(selector, wantsToBe, 'no')} disabled={selector === 0}>Doesn't want to be with</ToggleButton>
                        </ToggleButtonGroup>
                        <Button variant="link" onClick={reset}>Clear Selection</Button>
                    </div>
                    <div className="mb-3">
                        {pupils.map( pupil => (
                            <span key={pupil.id} className="d-inline-block mb-1 me-1">
                                {!selector
                                    ? <ToggleButton
                                        type="radio"
                                        variant="outline-primary"
                                        id={`selector-${pupil.id}`}
                                        value={pupil.id}
                                        onChange={(e) => setSelector(parseInt(e.target.value))}
                                        disabled={selector && selector !== pupil.id}
                                        checked={selector === pupil.id}
                                    >{pupil.firstName} {pupil.lastName}</ToggleButton>
                                    : <ToggleButton
                                        type="radio"
                                        variant={selector === pupil.id ? 'primary' : 'outline-primary'}
                                        id={`selected-${pupil.id}`}
                                        value={pupil.id}
                                        onChange={(e) => setSelected(parseInt(e.target.value))}
                                        disabled={selector === pupil.id}
                                        checked={selected === pupil.id}
                                    >{pupil.firstName} {pupil.lastName}</ToggleButton>}
                            </span>
                        ))}
                    </div>
                    <Button onClick={handleSave} disabled={!(selector && selected)}>Add Preference</Button>
                    <hr />
                    <Preferences group={group} items={mapPreferencesByPupils(pupils, preferences)} />
                </>
            )}
        </>
    );
};

const Instructions = ({ selector }) => {
    let message = '';

    if ( ! selector ) {
        message = 'Click on a pupil name';
    }

    if ( selector ) {
        message = 'Choose his preference and click on another pupil';
    }

    return message && <Alert variant="info">{message}</Alert>
}

const Preferences = ({ group, items }) => {
    return (
        <Row xs={2} md={3} lg={4} className="g-2">
            {Object.keys(items).map(selectorId => (
                <Col key={selectorId}>
                    <Card className="h-100">
                        <Card.Body>
                            <Card.Subtitle className="border-bottom mb-1 pb-1">{`${items[selectorId].name} `}</Card.Subtitle>
                            <Stack direction="horizontal" gap={2}>
                                {!objectIsEmpty(items[selectorId].yes) && (
                                    <span>
                                wants to be with:<br />
                                        {Object.keys(items[selectorId].yes)
                                            .map(selectedId => <Preference
                                                key={selectedId}
                                                groupId={group.id}
                                                selectorId={selectorId}
                                                selectedId={selectedId}
                                                selectedName={items[selectorId].yes[selectedId]}
                                            />)}
                            </span>
                                )}
                                {/** Show separator **/}
                                {!objectIsEmpty(items[selectorId].no) && !objectIsEmpty(items[selectorId].yes) && <div className="vr"></div>}
                                {!objectIsEmpty(items[selectorId].no) && (
                                    <span>
                                doesn't want to be with:<br />
                                        {Object.keys(items[selectorId].no)
                                            .map(selectedId => <Preference
                                                key={selectedId}
                                                groupId={group.id}
                                                selectorId={selectorId}
                                                selectedId={selectedId}
                                                selectedName={items[selectorId].no[selectedId]}
                                            />)}
                            </span>
                                )}
                            </Stack>
                        </Card.Body>
                    </Card>
                </Col>
            ))}
        </Row>
    );
};

const Preference = ({groupId, selectorId, selectedId, selectedName}) => {
    const [deleted, setDeleted] = useState(false);

    const handleDelete = async (e) => {
        e.preventDefault();

        const response = await Api.delete(
            `/groups/${groupId}/preferences`,
            { data: prepareData(selectorId, selectedId) });

        if ( response.status === 200 ) {
            setDeleted(true);
        }
    };

    if ( deleted ) {
        return null;
    }

    return (
        <Badge key={selectedId} className="me-1 mb-1" size="sm">
            <a href="#" onClick={handleDelete}><X color="white" /></a>
            {selectedName}
        </Badge>
    )
}

/**
 * Map the preferences by pupil selector ID
 * @param pupils
 * @param preferences
 * @returns object:
 *  {
 *      selectorId: {
 *          name: [string],
 *          yes: { selectorId: [string], [...]},
 *          no: { selectorId: [string], [...]}
 *      }
 *      ...
 *  }
 */
const mapPreferencesByPupils = (pupils, preferences) => {
    const map = {};

    preferences.forEach( preference => {
        const selectorId = preference.selectorSelectedId.selectorId;
        const selectedId = preference.selectorSelectedId.selectedId;
        const wantsToBe  = preference.isSelectorWantToBeWithSelected;

        // initialize an array for selector pupil with his name and map of selected pupils.
        if ( ! map[selectorId] ) {
            map[selectorId] = {
                name: pupils.find(findPupil(selectorId)).firstName,
                yes:  {},
                no:   {}
            };
        }

        const name = pupils.find(findPupil(selectedId))?.firstName;
        if ( name ) map[selectorId][wantsToBe ? 'yes' : 'no'][selectedId] = name;
    });

    return map;
};

const findPupil = (pupilId) => (pupil) => pupilId === pupil.id;

const buttonVariant = (selected, wantsToBe, equals) => {
    if ( selected && wantsToBe ) {
        if ( wantsToBe === 'yes' && equals === 'yes' ) {
            return 'success';
        }

        if ( wantsToBe === 'no' && equals === 'no' ) {
            return 'danger';
        }
    }

    return 'outline-secondary';
};

const prepareData = (selector, selected, wantsToBe) => {
    const data = {
        selectorSelectedId: {
            selectorId: selector,
            selectedId: selected,
        }
    };

    if ( wantsToBe ) {
        data['isSelectorWantToBeWithSelected'] = wantsToBe === 'yes';
    }

    return data;
}