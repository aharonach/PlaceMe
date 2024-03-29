import React, {useMemo, useState} from 'react';
import {
    Alert,
    Badge,
    Button,
    Card,
    Col, FormControl,
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
    // eslint-disable-next-line no-unused-vars
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
    const [filterPupil, setFilterPupil] = useState('');
    const [filterPreference, setFilterPreference] = useState('');

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

    const filterPupils = (e) => setFilterPupil(e.target.value.toLowerCase());
    const filterPreferences = (e) => setFilterPreference(e.target.value.toLowerCase());
    const mapped = useMemo(() => mapPreferencesByPupils(preferences), [preferences]);

    return (
        <>
            {errorFetch && <Alert variant="danger">{errorFetch}</Alert> }
            <>
                <h3>Preferences</h3>
                <Instructions />
                <Row>
                    <Col lg={4}>
                        <div className="mb-3">
                            <Button onClick={handleSave} disabled={!(selector && selected)}>Add Preference</Button>
                            <Loading show={loadingPupils} />
                        </div>
                        <div className="mb-3">
                            <ToggleButtons selector={selector} handleToggle={handleToggle} reset={reset} wantsToBe={wantsToBe} />
                        </div>
                        <Loading show={loadingFetch} block={false} size="sm" />
                        {!errorPupils && pupils && <>
                            <FormControl onChange={filterPupils} value={filterPupil} className="mb-2" placeholder="Search for a pupil by name or ID" />
                            <div className="d-flex flex-wrap">
                                <PupilButtons
                                    pupils={pupils}
                                    selector={selector}
                                    selected={selected}
                                    setSelected={setSelected}
                                    setSelector={setSelector}
                                    filter={filterPupil}
                                />
                            </div>
                        </>}
                    </Col>
                    <Col lg={8}>
                        <h4>Final Preferences</h4>
                        <FormControl onChange={filterPreferences} value={filterPreference} className="mb-2" placeholder="Filter..." />
                        <Preferences
                            group={group}
                            items={mapped}
                            updatePreferences={getPreferences}
                            filter={filterPreference}
                        />
                    </Col>
                </Row>
            </>
        </>
    );
};

const ToggleButtons = ({ wantsToBe, handleToggle, selector, reset}) => {
    return (
        <>
            <ToggleButtonGroup type="radio" name="wantsToBeWith" value={wantsToBe} onChange={handleToggle}>
                <ToggleButton id="wantsToBeWith" value="yes" variant={buttonVariant(selector, wantsToBe, 'yes')} disabled={selector === 0}>Prefers</ToggleButton>
                <ToggleButton id="doesntWantToBeWith" value="no" variant={buttonVariant(selector, wantsToBe, 'no')} disabled={selector === 0}>Doesn't Prefer</ToggleButton>
            </ToggleButtonGroup>
            {selector ? <Button variant="link" onClick={reset}>Clear Selection</Button> : null}
        </>
    );
}

const Instructions = () => {
    return (
        <Alert variant="success">
            <Alert.Heading as="h5">Instructions</Alert.Heading>
            <p><strong>Step 1:</strong> Search pupil for selector</p>
            <p><strong>Step 2:</strong> Choose the preference type (Prefers to be/Prefers not to be)</p>
            <p><strong>Step 3:</strong> Choose another pupil for selector preference</p>
        </Alert>
    )
}

const Preferences = ({ group, items, updatePreferences, filter }) => {
    return (
        <Row xs={2} md={3} lg={4} className="g-2">
            {Object.keys(items).map(selectorId => {
                const filtered = ! ( items[selectorId].name.toLowerCase().includes(filter) );

                if ( filtered ) {
                    return null;
                }

                return (
                    <Col key={selectorId}>
                        <Card className="h-100">
                            <Card.Body>
                                <Card.Subtitle
                                    className="border-bottom mb-1 pb-1">{`${items[selectorId].name} `}</Card.Subtitle>
                                <Stack direction="vertical" gap={2}>
                                    {!objectIsEmpty(items[selectorId].yes) && (
                                        <Stack direction="vertical" gap={1}>
                                            <span className="text-success">Prefers:</span>
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
                                    {!objectIsEmpty(items[selectorId].no) && (
                                        <Stack direction="vertical" gap={1}>
                                            <span className="text-danger">Doesn't prefer:</span><br/>
                                            <span>{Object.keys(items[selectorId].no).map(selectedId => <Preference
                                                key={selectedId}
                                                groupId={group.id}
                                                selectorId={selectorId}
                                                selectedId={selectedId}
                                                selectedName={items[selectorId].no[selectedId]}
                                                updatePreferences={updatePreferences}
                                            />)}</span>
                                        </Stack>
                                    )}
                                </Stack>
                            </Card.Body>
                        </Card>
                    </Col>
                )
            })}
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
        <Badge key={selectedId} className="me-1 mb-1 d-inline-flex align-items-center text-wrap" size="sm">
            <Button variant="link" onClick={handleDelete} title="Delete Preference" className="lh-1 p-0">
                <X color="white" size={20} aria-hidden="true" />
            </Button> {selectedName}
        </Badge>
    )
}

const PupilButtons = ({ pupils, selector, setSelector, selected, setSelected, filter }) => {
    return pupils.map( pupil => {
        const filtered = filter &&
            (pupil.firstName.toLowerCase().includes(filter)
                || pupil.lastName.toLowerCase().includes(filter)
                || pupil.givenId.toLowerCase().includes(filter) );

        if ( ! filtered ) {
            return null;
        }

        return (
            <span key={pupil.id}
                  className="mb-1 me-1 d-inline-block"
            >
            {!selector
                ? <ToggleButton
                    size="sm"
                    type="radio"
                    variant="outline-primary"
                    id={`selector-${pupil.id}`}
                    value={pupil.id}
                    onChange={(e) => setSelector(parseInt(e.target.value))}
                    disabled={selector && selector !== pupil.id}
                    checked={selector === pupil.id}
                >{pupil.firstName} {pupil.lastName}</ToggleButton>
                : <ToggleButton
                    size="sm"
                    type="radio"
                    variant={selector === pupil.id ? 'primary' : 'outline-primary'}
                    id={`selected-${pupil.id}`}
                    value={pupil.id}
                    onChange={(e) => setSelected(parseInt(e.target.value))}
                    disabled={selector === pupil.id}
                    checked={selected === pupil.id}
                >{pupil.firstName} {pupil.lastName}</ToggleButton>}
        </span>
        )
    });
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
                name: `${preference.selectorFirstName} ${preference.selectorLastName}`,
                yes:  {},
                no:   {}
            };
        }

        map[selectorId][wantsToBe ? 'yes' : 'no'][selectedId] = `${preference.selectedFirstName} ${preference.selectedLastName}`;
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