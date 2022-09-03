import React, {useMemo, useState} from 'react';
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
    const [preferences, errorFetch, loadingFetch, axiosFetch, getPreferences] = useFetchList({
        fetchUrl: `/groups/${group.id}/preferences`,
        propertyName: "preferenceDtoList"
    });
    const [pupils, errorPupils, loadingPupils] = useFetchList({
        fetchUrl: `/groups/${group.id}/pupils/all`,
        propertyName: "pupilList"
    });
    const [selector, setSelector] = useState(0);
    const [selected, setSelected] = useState(0);
    const [wantsToBe, setWantsToBe] = useState('yes');

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
            getPreferences();
        }
    }

    const mapped = useMemo(() => mapPreferencesByPupils(preferences), [preferences]);

    return (
        <>
            {errorFetch && <Alert variant="danger">{errorFetch}</Alert> }
            <>
                <h3>Preferences</h3>
                <Instructions selector={selector} />
                <div className="mb-3">
                    <Loading show={loadingPupils} />
                    {!errorPupils && pupils && <PupilButtons
                        pupils={pupils}
                        selector={selector}
                        selected={selected}
                        setSelected={setSelected}
                        setSelector={setSelector}
                    />}
                </div>
                <div className="mb-3">
                    <ToggleButtonGroup type="radio" name="wantsToBeWith" value={wantsToBe} onChange={handleToggle}>
                        <ToggleButton id="wantsToBeWith" value="yes" variant={buttonVariant(selector, wantsToBe, 'yes')} disabled={selector === 0}>Wants to be with</ToggleButton>
                        <ToggleButton id="doesntWantToBeWith" value="no" variant={buttonVariant(selector, wantsToBe, 'no')} disabled={selector === 0}>Doesn't want to be with</ToggleButton>
                    </ToggleButtonGroup>
                    {selector ? <Button variant="link" onClick={reset}>Clear Selection</Button> : null}
                </div>
                <Button onClick={handleSave} disabled={!(selector && selected)}>Add Preference</Button>
                <Loading show={loadingFetch} block={false} size="sm" />
                <hr />
                <Preferences group={group} items={mapped} updatePreferences={getPreferences} />
            </>
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

const Preferences = ({ group, items, updatePreferences }) => {
    return (
        <Row xs={2} md={3} className="g-2">
            {Object.keys(items).map(selectorId => (
                <Col key={selectorId}>
                    <Card className="h-100">
                        <Card.Body>
                            <Card.Subtitle className="border-bottom mb-1 pb-1">{`${items[selectorId].name} `}</Card.Subtitle>
                            <Stack direction="horizontal" gap={2}>
                                {!objectIsEmpty(items[selectorId].yes) && (
                                    <Stack direction="vertical" gap={1}>
                                        <span>wants to be with:</span>
                                        <span>
                                            {Object.keys(items[selectorId].yes).map(selectedId => <Preference
                                                key={selectedId}
                                                groupId={group.id}
                                                selectorId={selectorId}
                                                selectedId={selectedId}
                                                selectedName={items[selectorId].yes[selectedId]}
                                                updatePreferences={updatePreferences}
                                            />)}
                                        </span>
                                    </Stack>
                                )}
                                {/** Show separator **/}
                                {!objectIsEmpty(items[selectorId].no) && !objectIsEmpty(items[selectorId].yes) && <div className="vr"></div>}
                                {!objectIsEmpty(items[selectorId].no) && (
                                    <Stack direction="vertical" gap={1}>
                                        <span>doesn't want to be with:</span><br />
                                        <span>{Object.keys(items[selectorId].no).map(selectedId => <Preference
                                                key={selectedId}
                                                groupId={group.id}
                                                selectorId={selectorId}
                                                selectedId={selectedId}
                                                selectedName={items[selectorId].no[selectedId]}
                                            />)}</span>
                                    </Stack>
                                )}
                            </Stack>
                        </Card.Body>
                    </Card>
                </Col>
            ))}
        </Row>
    );
};

const Preference = ({groupId, selectorId, selectedId, selectedName, updatePreferences}) => {
    const handleDelete = async (e) => {
        e.preventDefault();

        const response = await Api.delete(
            `/groups/${groupId}/preferences`,
            { data: prepareData(selectorId, selectedId) });

        if ( response.status === 200 ) {
            updatePreferences();
        }
    };

    return (
        <Badge key={selectedId} className="me-1 mb-1 d-inline-flex align-items-center" size="sm">
            <a href="#" onClick={handleDelete}><X color="white" size={20} /></a>
            {selectedName}
        </Badge>
    )
}

const PupilButtons = ({ pupils, selector, setSelector, selected, setSelected }) => {
    return pupils.map( pupil => (
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
    ));
}

/**
 * Map the preferences by pupil selector ID
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
const mapPreferencesByPupils = (preferences) => {
    const map = {};

    preferences?.forEach( preference => {
        const selectorId = preference.selectorSelectedId.selectorId;
        const selectedId = preference.selectorSelectedId.selectedId;
        const wantsToBe  = preference.isSelectorWantToBeWithSelected;

        // initialize an array for selector pupil with his name and map of selected pupils.
        if ( ! map[selectorId] ) {
            map[selectorId] = {
                name: preference.selectorFirstName,
                yes:  {},
                no:   {}
            };
        }

        map[selectorId][wantsToBe ? 'yes' : 'no'][selectedId] = preference.selectedFirstName;
    });

    return map;
};

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